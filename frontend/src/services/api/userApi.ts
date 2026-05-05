import instance from "./customize";
import type { AxiosResponse } from "axios";
import type { UserRequest, UserType } from "../../types/user";

export const UserApi = {
  feature: "users",

  async getInfo(): Promise<AxiosResponse<UserType, any>> {
    return await instance.get(`/${this.feature}/info`);
  },

  async handleLogin(
    request: UserRequest,
  ): Promise<AxiosResponse<UserType, any>> {
    return await instance.post<UserType>(`/${this.feature}/login`, {
      username: request.username,
      password: request.password,
    });
  },

  async handleLogout(): Promise<AxiosResponse<any, any>> {
    return await instance.post(`/${this.feature}/logout`);
  },
};
