import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { HttpStatusCode } from "axios";
import { UserService } from "../services/userService";
import type { UserRequest, UserType } from "../types/user";

interface AuthContextType {
  user: UserType | undefined;
  isLoggedIn: boolean;
  loading: boolean;
  fetchUser: () => Promise<void>;
  login: (request: UserRequest) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserType | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchUser = async () => {
    const response = await UserService.getInfo();
    if (response.status === HttpStatusCode.Ok && response.data) {
      setUser(response.data);
    } else if ((response as any).error) {
      console.error((response as any).message);
      setUser(undefined);
    }

    setLoading(false);
  };
  const login = async (request: UserRequest) => {
    const response = await UserService.handleLogin(request);
    if (response.status === HttpStatusCode.Ok && response.data) {
      await fetchUser();
    } else if ((response as any).error) {
      console.error((response as any).message);
      setUser(undefined);
    }
  };
  const logout = async () => {
    const response = await UserService.handleLogout();
    if (response.status === HttpStatusCode.Ok) {
      setUser(undefined);
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoggedIn: !!user,
        loading,
        fetchUser,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");

  return context;
}
