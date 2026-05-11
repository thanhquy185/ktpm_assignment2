import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { HttpStatusCode } from "axios";
import { useAuth } from "../contexts/AuthContext";
import { UserApi } from "../services/api/userApi";
import type { UserRequest } from "../types/user";

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { fetchUser } = useAuth();

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!username.trim()) {
      setError("Vui lòng nhập tên đăng nhập");
      return;
    }
    if (!password.trim()) {
      setError("Vui lòng nhập mật khẩu");
      return;
    }

    setLoading(true);
    setError("");

    const response = await UserApi.handleLogin({
      username,
      password,
    } as UserRequest);
    if (response.status === HttpStatusCode.Ok && response.data) {
      await fetchUser();
      toast.success("Đăng nhập thành công!");
      navigate("/products");
    } else if ((response as any).error) {
      setError("Sai tên tài khoản hoặc mật khẩu!");
      toast.error("Đăng nhập thất bại!");
    }

    setLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
      <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-gray-900 mb-6 text-center">
          Đăng nhập
        </h1>
        <form onSubmit={handleSubmit}>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tên tài khoản
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Nhập tên tài khoản"
              data-testid="login-username-input"
              className="w-full px-4 py-2 border rounded-lg"
            />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Mật khẩu
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Nhập mật khẩu"
              data-testid="login-password-input"
              className="w-full px-4 py-2 border rounded-lg"
            />
          </div>
          {error && (
            <div className="text-red-600 text-sm bg-red-50 p-2 rounded mt-4">
              {error}
            </div>
          )}
          <button
            type="submit"
            disabled={loading}
              data-testid="login-button"
            className="w-full bg-indigo-600 text-white py-2 mt-10 rounded-lg"
          >
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
