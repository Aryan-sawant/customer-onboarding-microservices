import os
import requests
import google.generativeai as genai
from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, date, timedelta
import json

# --- Configuration ---
app = Flask(__name__)
CORS(app, resources={r"/chat": {"origins": "http://localhost:8080"}}) 
genai.configure(api_key=os.environ.get("GOOGLE_API_KEY"))
SPRING_BOOT_API_URL = "http://localhost:8080/api/chatbot" 

# --- Tool Definitions ---
get_customer_and_account_details_tool = {
    "name": "get_customer_and_account_details",
    "description": "Use this function for ANY query about a specific customer, such as 'get account details of aryan', 'what is aryan's pan?', or 'show me aryan's nominee'. This is the main tool to find a person and retrieve all their associated KYC and account information.",
    "parameters": {
        "type": "OBJECT", "properties": {"search_term": {"type": "STRING", "description": "The customer's name, email, ID, PAN, or Aadhaar mentioned in the user's query."}}, "required": ["search_term"]
    }
}
get_pan_tool = {"name": "get_pan", "description": "Get the PAN number for the customer currently in the conversation context."}
get_aadhaar_tool = {"name": "get_aadhaar", "description": "Get the Aadhaar number for the customer currently in the conversation context."}
get_nominee_details_tool = {"name": "get_nominee_details", "description": "Get the nominee details for the customer currently in the conversation context."}
get_dashboard_statistics_tool = {"name": "get_dashboard_statistics", "description": "Get key statistics from the admin dashboard, like total, pending, verified, and rejected application counts."}
list_applicants_by_kyc_status_tool = {"name": "list_applicants_by_kyc_status", "description": "List the names and IDs of up to 5 applicants with a specific KYC status.", "parameters": {"type": "OBJECT", "properties": {"kyc_status": {"type": "STRING", "enum": ["PENDING", "VERIFIED", "REJECTED"]}}, "required": ["kyc_status"]}}

list_registrations_by_date_tool = {
    "name": "list_registrations_by_date",
    "description": "Get a list of customers who registered on a specific date. Use for queries like 'who registered today?' or 'show me yesterday's registrations'.",
    "parameters": {"type": "OBJECT", "properties": {"date_query": {"type": "STRING", "description": "A date reference like 'today', 'yesterday', or a specific date in YYYY-MM-DD format."}}, "required": ["date_query"]}
}
list_accounts_created_by_date_tool = {
    "name": "list_accounts_created_by_date",
    "description": "Get a list of bank accounts that were created on a specific date. Use for queries like 'what accounts were created today?' or 'show me accounts from yesterday'.",
    "parameters": {"type": "OBJECT", "properties": {"date_query": {"type": "STRING", "description": "A date reference like 'today', 'yesterday', or a specific date in YYYY-MM-DD format."}}, "required": ["date_query"]}
}

# --- Intelligent Formatters ---
def format_registration_list(registrations, date_str):
    if not registrations: return f"No new customers registered on {date_str}."
    response_lines = [f"Here are the customers who registered on {date_str}:"]
    for reg in registrations:
        line = f"- {reg['fullName']} (Application ID: {reg['id']})"
        response_lines.append(line)
    return "\n".join(response_lines)

def format_account_list(accounts, date_str):
    if not accounts: return f"No new accounts were created on {date_str}."
    response_lines = [f"Here are the accounts created on {date_str}:"]
    for acc in accounts:
        line = f"- Account #{acc['accountNumber']} ({acc['accountType']}) for Customer ID: {acc['customerId']}"
        response_lines.append(line)
    return "\n".join(response_lines)

def format_full_details(data, search_term):
    if not data: return f"I could not find any customer or applicant matching '{search_term}'."
    kyc_app = data.get('kycApplication')
    if not kyc_app: return f"I could not find any customer or applicant matching '{search_term}'."
    customer, account = data.get('customer', kyc_app), data.get('account')
    name = customer.get('fullName')
    kyc_status = customer.get('kycStatus')
    response_lines = [f"Found details for **{name}** (Application ID: {kyc_app.get('id', 'N/A')}):", f"- **KYC Status:** {kyc_status}", f"- **PAN:** {customer.get('pan', 'N/A')}", f"- **Aadhaar:** {customer.get('aadhaar', 'N/A')}", f"- **Phone:** {customer.get('phone', 'N/A')}"]
    if account:
        response_lines.extend(["\n**Account Information:**", f"- **Account Status:** {account.get('accountStatus', 'N/A')}", f"- **Account Number:** {account.get('accountNumber', 'N/A')}"])
    elif kyc_status == 'VERIFIED': response_lines.append("\n**Account Information:** This customer is verified, but their bank account has not been created yet.")
    else: response_lines.append("\nThis applicant does not have a bank account yet.")
    return "\n".join(response_lines)

def format_specific_detail(data, detail_key, human_readable_name):
    customer = data.get('customer', data.get('kycApplication'))
    if not customer: return "You need to find a customer first."
    name = customer.get('fullName')
    value = customer.get(detail_key)
    if value: return f"The {human_readable_name} for {name} is: **{value}**"
    return f"I could not find the {human_readable_name} for {name}."

def format_nominee_details(data):
    customer = data.get('customer')
    if not customer: return "You need to find a customer first."
    name = customer.get('fullName')
    nominee = customer.get('nominee')
    if nominee and nominee.get('name'): return f"The nominee for {name} is **{nominee['name']}** (Phone: {nominee.get('mobile', 'N/A')})."
    return f"{name} has not registered a nominee."

# *** THE DEFINITIVE FIX: A STRICT, GOOGLE-COMPLIANT HISTORY SERIALIZER ***
def serialize_history(history):
    if not history: return []
    serializable_history = []
    for content in history:
        if not content: continue
        
        role = "model"
        if hasattr(content, 'role') and content.role:
            role = "user" if "user" in content.role.lower() else content.role

        parts_list = []
        if hasattr(content, 'parts') and content.parts:
            for part in content.parts:
                if not part: continue
                # The key insight: function_call and function_response are mutually exclusive with text.
                # A single Part can only be one of these.
                if hasattr(part, 'function_call') and part.function_call and part.function_call.name:
                    fc = part.function_call
                    args = dict(fc.args) if hasattr(fc, 'args') and fc.args else {}
                    parts_list.append({'function_call': {'name': fc.name, 'args': args}})
                elif hasattr(part, 'function_response') and part.function_response and part.function_response.name:
                    fr = part.function_response
                    response_content = fr.response or {}
                    data_to_serialize = response_content.get('data') if isinstance(response_content.get('data'), dict) else None
                    parts_list.append({'function_response': {'name': fr.name, 'response': {'result': response_content.get('result'), 'data': data_to_serialize}}})
                elif hasattr(part, 'text') and part.text:
                    parts_list.append({'text': part.text})
        
        if parts_list:
            serializable_history.append({'role': role, 'parts': parts_list})
    return serializable_history

# --- CHATBOT LOGIC ---
@app.route('/chat', methods=['POST'])
def chat():
    data = request.json
    user_query = data.get('query')
    history = data.get('history', [])
    
    tools_for_model = [
        get_customer_and_account_details_tool, get_pan_tool, get_aadhaar_tool, 
        get_nominee_details_tool, get_dashboard_statistics_tool, 
        list_applicants_by_kyc_status_tool, list_registrations_by_date_tool, 
        list_accounts_created_by_date_tool
    ]
    
    try:
        model = genai.GenerativeModel(model_name='gemini-2.5-flash', tools=tools_for_model)
        
        last_customer_data = None
        for entry in reversed(history):
            if entry.get('role') == 'function' and entry.get('name') == 'get_customer_and_account_details':
                if entry.get('response') and entry['response'].get('data'):
                    last_customer_data = entry['response']['data']
                    break

        chat_session = model.start_chat(history=history)
        response = chat_session.send_message(user_query)
        
        if response.candidates and response.candidates[0].content.parts and response.candidates[0].content.parts[0].function_call:
            function_call = response.candidates[0].content.parts[0].function_call
            tool_name = function_call.name
            
            api_response_data, api_response_text = None, "An unknown error occurred."
            auth_credentials = ('internal-user', 'internal-password')

            if tool_name == 'get_customer_and_account_details':
                search_term = function_call.args.get('search_term', '')
                api_url = f"{SPRING_BOOT_API_URL}/admin/search-customer?keyword={search_term}"
                api_res = requests.get(api_url, auth=auth_credentials)
                if api_res.status_code == 200 and api_res.text:
                    api_response_data = api_res.json()
                    api_response_text = format_full_details(api_response_data, search_term)
                else:
                    api_response_text = f"I could not find anyone matching '{search_term}'."

            elif tool_name in ['get_pan', 'get_aadhaar', 'get_nominee_details']:
                if last_customer_data:
                    api_response_data = last_customer_data
                    if tool_name == 'get_pan': api_response_text = format_specific_detail(last_customer_data, 'pan', 'PAN number')
                    elif tool_name == 'get_aadhaar': api_response_text = format_specific_detail(last_customer_data, 'aadhaar', 'Aadhaar number')
                    elif tool_name == 'get_nominee_details': api_response_text = format_nominee_details(last_customer_data)
                else:
                    api_response_text = "You need to find a customer first. Please ask me to search for someone."
            
            elif tool_name == 'get_dashboard_statistics':
                api_res = requests.get(f"{SPRING_BOOT_API_URL}/admin/dashboard-stats", auth=auth_credentials)
                data = api_res.json()
                api_response_text = f"Stats: Total: {data['total']}, Pending: {data['pending']}, Verified: {data['verified']}, Rejected: {data['rejected']}."

            elif tool_name == 'list_applicants_by_kyc_status':
                status = function_call.args['kyc_status']
                api_res = requests.get(f"{SPRING_BOOT_API_URL}/admin/list-by-kyc?status={status}", auth=auth_credentials)
                data = api_res.json()
                if data:
                    names = [f"- {c['fullName']} (ID: {c['id']})" for c in data]
                    api_response_text = f"Found applicants with status '{status}':\n" + "\n".join(names)
                else:
                    api_response_text = f"No applicants found with status '{status}'."

            elif tool_name == 'list_registrations_by_date' or tool_name == 'list_accounts_created_by_date':
                date_query = function_call.args.get('date_query', 'today').lower()
                target_date = date.today()
                if 'yesterday' in date_query:
                    target_date = date.today() - timedelta(days=1)
                
                date_str = target_date.isoformat() # Format as YYYY-MM-DD
                
                endpoint = "registrations-on-date" if tool_name == 'list_registrations_by_date' else "accounts-created-on-date"
                api_url = f"{SPRING_BOOT_API_URL}/admin/{endpoint}?date={date_str}"
                
                api_res = requests.get(api_url, auth=auth_credentials)
                
                if api_res.status_code == 200 and api_res.text:
                    data = api_res.json()
                    if tool_name == 'list_registrations_by_date':
                        api_response_text = format_registration_list(data, date_query)
                    else:
                        api_response_text = format_account_list(data, date_query)
                else:
                    api_response_text = f"Sorry, I could not retrieve the list for {date_query}."

            final_response = chat_session.send_message(
                genai.protos.Part(function_response=genai.protos.FunctionResponse(
                    name=tool_name,
                    response={"result": api_response_text, "data": api_response_data}
                ))
            )
            
            return jsonify({"response": final_response.text, "history": serialize_history(chat_session.history)})
        else:
            return jsonify({"response": response.text, "history": serialize_history(chat_session.history)})

    except Exception as e:
        print(f"An unexpected error occurred in the chat logic: {e}")
        return jsonify({"response": f"An error occurred: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(port=5000, debug=True)