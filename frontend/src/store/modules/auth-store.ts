import { defineStore } from "pinia";
interface IState {
  accessToken: string;
  refreshToken: string;
  userId: string;
}
export const useAuthStore = defineStore("auth", {
  state: (): IState => {
    return {
      accessToken: "",
      refreshToken: "",
      userId: "",
    };
  },

  persist: true,
});
