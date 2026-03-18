<template>
  <div class="login-page">
    <div class="login-panel">
      <h2 class="title">高校智能助手管理信息系统</h2>

      <!-- 账号 -->
      <div class="input-box account-box">
        <img src="/account.png" alt="account" />
        <input
          v-model="account"
          placeholder="请输入账号"
        />
      </div>

      <!-- 密码 -->
      <div class="input-box password-box">
        <img src="/password.png" alt="password" />
        <input
          v-model="password"
          type="password"
          placeholder="请输入密码"
        />
      </div>

      <!-- 忘记密码 -->
      <div class="options">
        <a href="javascript:void(0)">忘记密码</a>
      </div>

      <!-- 登录按钮 -->
      <button class="login-btn" @click="login" :disabled="loading">
        {{ loading ? "登录中..." : "登录" }}
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup name="Login">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAppStore } from "@/stores/app";

const router = useRouter();
const appStore = useAppStore();

const account = ref("");
const password = ref("");
const loading = ref(false);

const login = async () => {
  if (!account.value.trim()) {
    alert("请输入账号");
    return;
  }

  if (!password.value.trim()) {
    alert("请输入密码");
    return;
  }

  try {
    loading.value = true;
    await appStore.login(account.value, password.value);
    await appStore.bootstrap();
    router.push("/home");
  } catch (e: any) {
    alert(e?.message || "账号或密码错误");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(217, 234, 252);
}

/* 白色登录面板 */
.login-panel {
  width: 300px;
  padding: 30px 40px;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 25px;
  font-weight: 600;
}

.input-box {
  display: flex;
  align-items: center;
  border: 1px solid #dcdcdc;
  border-radius: 6px;
  padding: 8px 10px;
  /* margin-bottom: 20px; */
  transition: border-color 0.3s;
  /* background-color: aqua; */
}

.account-box {
  margin-bottom: 20px;
}

.password-box {
  margin-bottom: 5px;
}

.input-box:hover {
  border-color: #409eff;
}

.input-box img {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.input-box input {
  border: none;
  outline: none;
  flex: 1;
  font-size: 14px;
}

.options {
  text-align: left;
  margin-bottom: 10px;
}

.options a {
  font-size: 13px;
  color: #409eff;
  text-decoration: none;
}

.options a:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 40px;
  background-color: #409eff;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
}

.login-btn:hover {
  background-color: #337ecc;
}
</style>
