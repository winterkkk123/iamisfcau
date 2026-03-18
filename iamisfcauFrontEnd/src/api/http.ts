import axios, { type AxiosRequestConfig, type InternalAxiosRequestConfig } from "axios";

export const http = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 15000,
});

http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStr = localStorage.getItem("loginUser");

    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user?.id) {
          config.headers["X-User-Id"] = user.id;
        }
      } catch {
        localStorage.removeItem("loginUser");
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

http.interceptors.response.use(
  (res) => res,
  (err) => {
    const msg =
      err?.response?.data?.message ||
      err?.response?.data?.detail ||
      err?.response?.data?.error ||
      err.message ||
      "请求失败";
    return Promise.reject(new Error(msg));
  }
);

export async function apiGet<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res = await http.get<any, { data: T }>(url, config);
  return res.data;
}

export async function apiPost<T>(
  url: string,
  data?: any,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await http.post<any, { data: T }>(url, data, config);
  return res.data;
}

export async function apiPut<T>(
  url: string,
  data?: any,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await http.put<any, { data: T }>(url, data, config);
  return res.data;
}

export async function apiDelete<T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await http.delete<any, { data: T }>(url, config);
  return res.data;
}