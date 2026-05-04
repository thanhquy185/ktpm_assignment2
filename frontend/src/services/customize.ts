import axios from "axios";
import Cookies from "js-cookie";

const instance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

instance.interceptors.request.use(
  function (config) {
    const token = Cookies.get("refreshToken"); // lấy token mới nhất mỗi lần gọi
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  },
);

instance.interceptors.response.use(
  function (response) {
    if (response.data && response.data.data) return response.data;
    return response;
  },
  function (error) {
    if (error.response && error.response.data) return error.response.data;
    return Promise.reject(error);
  },
);

export default instance;
