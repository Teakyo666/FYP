import axios from 'axios'
import request from "@/utils/request";

// Create large model API instance
const bigModelApiClient = axios.create({
  baseURL: 'https://open.bigmodel.cn/api/llm-application/open/v3',
  timeout: 30000,
  headers: {
    'Authorization': 'Bearer 7c7b3831071a4ce181aad2e8e4e2abdd.8jgOkV4dWxVqBteP',
    'Content-Type': 'application/json'
  }
})

/**
 * Run garbage classification check (new large model API)
 * @param {string} question - User input for garbage type question
 * @returns {Promise} API response stream
 */
export const runGarbageClassification = async (question) => {
  try {
    // 使用axios发送POST请求
    const response = await bigModelApiClient.post('/application/invoke', {
      app_id: '2002573831088312320',
      stream: false,
      send_log_event: false,
      messages: [
        {
          role: 'user',
          content: [
            {
              key: '输入项1',
              value: question,
              type: 'input'
            }
          ]
        }
      ]
    });
    
    return response.data;
  } catch (error) {
    throw error;
  }
}

export function AIGarbage(data) {
  return request({
    url: "AI/create",
    method: "post",
    data: data,
  });
}

export function AIGarbageList(data) {
  return request({
    url: "AI/list",
    method: "get",
    params: data,
  });
}

export function AIGarbageupdate(data) {
  return request({
    url: "AI/update",
    method: "post",
    data: data,
  });
}