import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8081/api',
});

export default apiClient;
