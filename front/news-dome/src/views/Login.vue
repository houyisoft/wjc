<template>
  <div class="login-wrap">
    <p class="web-title">新闻管理系统</p>
    <el-form
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="ruleForm"
      label-width="auto"
      class="demo-ruleForm"
    >
      <!-- 输入账号 / 邮箱 -->
      <el-form-item label="" prop="username">
        <el-input
          prefix-icon="el-icon-user"
          v-model="ruleForm.username"
          placeholder="请输入用户名或电子邮箱"
        ></el-input>
      </el-form-item>

      <!-- 输入密码 -->
      <el-form-item label="" prop="password">
        <el-input
          prefix-icon="el-icon-lock"
          type="password"
          v-model="ruleForm.password"
          placeholder="请输入密码"
          autocomplete="off"
        ></el-input>
      </el-form-item>

      <!-- 登录账户 -->
      <el-form-item>
        <el-button
          class="submit-btn"
          type="primary"
          @click="submitForm"
          :loading="loading"
          >登 录</el-button
        >
      </el-form-item>

      <!--  -->
      <p class="other">
        <el-checkbox v-model="remember">remember me</el-checkbox>
        <router-link to="/register">注册账户</router-link>
      </p>
    </el-form>
  </div>
</template>

<script>
import cookie from "vue-cookies";
import { apiLogin } from "@/api/index";
export default {
  data() {
    // 验证账户输入
    var username = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入用户名或邮箱"));
      }
      callback();
    };
    // 验证密码输入
    var password = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      }
      callback();
    };
    return {
      ruleForm: {
        username: "",
        password: "",
      },
      remember: false,
      rules: {
        username: [{ validator: username, trigger: "blur" }],
        password: [{ validator: password, trigger: "blur" }],
      },
      loading: false,
    };
  },
  methods: {
    // 登录账户
    async submitForm() {
      this.loading = true;
      let res = await apiLogin(this.ruleForm);
      if (res.data.code === 0) {
        this.$message.success("登录成功");
        if (this.remember) {
          // 使用 Cookie 记录，有效期一个月
          cookie.set("newsToken", res.data.token, "1m");
        } else {
          // 使用 sessionStorage 记录 token，当页面关闭时清除
          sessionStorage.newsToken = res.data.token;
        }
        this.$router.push({
          path: "/",
        });
      } else {
        this.$message.warning(res.data.msg);
      }
      this.loading = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.login-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  & > .web-title {
    font-size: 36px;
    margin-bottom: 36px;
  }
  & > .demo-ruleForm {
    width: 320px;
    .submit-btn {
      width: 100%;
    }
    & > .other {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
