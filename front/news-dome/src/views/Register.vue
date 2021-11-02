<template>
  <div class="login-wrap">
    <p class="page-title">注册账户</p>
    <el-form
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="ruleForm"
      label-width="auto"
      class="demo-ruleForm"
    >
      <!-- 输入账号 -->
      <el-form-item label="" prop="username">
        <el-input
          prefix-icon="el-icon-user"
          v-model="ruleForm.username"
          placeholder="请输入用户名"
        ></el-input>
      </el-form-item>

      <!-- 输入密码 -->
      <el-form-item label="" prop="password">
        <el-input
          prefix-icon="el-icon-lock"
          type="password"
          v-model="ruleForm.password"
          placeholder="请输入密码"
          :show-password="true"
        ></el-input>
      </el-form-item>

      <!-- 确认密码 -->
      <el-form-item label="" prop="again">
        <el-input
          prefix-icon="el-icon-lock"
          type="password"
          v-model="ruleForm.again"
          placeholder="请确认密码"
          :show-password="true"
        ></el-input>
      </el-form-item>

      <!-- 输入邮件 -->
      <el-form-item label="" prop="email">
        <el-input
          prefix-icon="el-icon-message"
          v-model.number="ruleForm.email"
          placeholder="请输入邮箱"
        ></el-input>
      </el-form-item>

      <!-- 提交注册信息 -->
      <el-form-item>
        <el-button
          class="submit-btn"
          type="primary"
          @click="submitForm('ruleForm')"
          :loading="loading"
          >注 册</el-button
        >
      </el-form-item>

      <!--  -->
      <p class="other">
        <span></span>
        <router-link to="/login">返回登录</router-link>
      </p>
    </el-form>
  </div>
</template>

<script>
import { apiPost } from "@/api/index";
export default {
  data() {
    // 用户名格式验证
    var validateUsername = (rule, value, callback) => {
      if (value.length < 5 || value.length > 20) {
        callback(new Error("用户名长度：5~20 个字符"));
      } else if (!/^[\da-z]+$/.test(value)) {
        callback(new Error("仅支持字母和数字"));
      }
      callback();
    };
    // 密码格式验证
    var validatePass = (rule, value, callback) => {
      if (!/^[\da-zA-Z@#$%^&*-+]+$/.test(value)) {
        callback(new Error("只能由大小写字母、数字和@#$%^&*-+等字符组成"));
      } else if (value.length < 8 || value.length > 20) {
        callback(new Error("密码长度：8~20 个字符"));
      } else if (!/[A-Z]+/.test(value)) {
        callback(new Error("至少包含一个大写字母"));
      } else if (!/[a-z]+/.test(value)) {
        callback(new Error("至少包含一个小写字母"));
      } else if (!/[\d]+/.test(value)) {
        callback(new Error("至少包含一个数字"));
      } else if (!/[@#$%^&*-+]+/.test(value)) {
        callback(new Error("至少包含一个特殊字符：@#$%^&*-+"));
      }
      callback();
    };
    // 确认密码验证
    var validateAgain = (rule, value, callback) => {
      if (value !== this.ruleForm.password) {
        callback(new Error("两次输入不一致"));
      }
      callback();
    };
    // 邮箱格式验证
    var validateEmail = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("邮箱不能为空"));
      } else if (
        !/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,}$/.test(
          value
        )
      ) {
        callback(new Error("邮箱格式错误"));
      }
      callback();
    };
    return {
      ruleForm: {
        username: "",
        password: "",
        again: "",
        email: "",
      },
      rules: {
        username: [{ validator: validateUsername, trigger: "blur" }],
        password: [{ validator: validatePass, trigger: "blur" }],
        again: [{ validator: validateAgain, trigger: "blur" }],
        email: [{ validator: validateEmail, trigger: "blur" }],
      },
      loading: false
    };
  },
  methods: {
    // 提交注册信息
    submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.loading = true;
          let res = await apiPost("renren-fast/app/register", {
            username: this.ruleForm.username,
            password: this.ruleForm.password,
            email: this.ruleForm.email,
          });
          if (res.data.code == 0) {
            this.$message.success("注册成功，请前往登录");
          }
          this.loading = false;
        } else {
          console.log("error submit!!");
          return false;
        }
      });
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
  & > .page-title {
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
