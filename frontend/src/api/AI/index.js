import axios from 'axios'
import request from "@/utils/request";

// 创建大模型API实例
const bigModelApiClient = axios.create({
  baseURL: 'https://open.bigmodel.cn/api/llm-application/open/v3',
  timeout: 30000,
  headers: {
    'Authorization': 'Bearer 7c7b3831071a4ce181aad2e8e4e2abdd.8jgOkV4dWxVqBteP',
    'Content-Type': 'application/json'
  }
})

/**
 * 运行垃圾分类检查 (新的大模型API)
 * @param {string} question - 用户输入的垃圾类型问题
 * @returns {Promise} API 响应流
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