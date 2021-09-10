import axios from 'axios';

const baseURL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL,
});

export default apiClient;
