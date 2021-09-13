import axios from 'axios';

const baseURL = (window as any).ENV.REACT_APP_API_BASE_URL || 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL,
});

apiClient.interceptors.response.use((response) => response, (error) => {
  const res = error.response;
  if (res == null) {
    alert('알 수 없는 문제가 발생했습니다.');
    throw error;
  }
  const msg = res.data?.msg;
  if (msg == null) {
    alert('알 수 없는 문제가 발생했습니다.');
    throw error;
  }
  alert(msg);
  throw error;
});

export default apiClient;
