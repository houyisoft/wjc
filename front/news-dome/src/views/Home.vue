<template>
  <div class="home-wrap">
    <!-- 头部区域 -->
    <header>
      <div class="header-wrap content">
        <p class="web-title">新闻管理系统</p>
        <p class="userinfo">
          <span v-if="login" class="info-box">
            <span> {{ userInfo.username }} </span>
            <span>-</span>
            <span> {{ userInfo.email }} </span>
          </span>
          <span v-if="login" class="logout" @click="logout">退出登录</span>
          <router-link v-else to="/login">登录账户</router-link>
        </p>
      </div>
    </header>
    <!-- / 头部区域 -->

    <!-- 主体区域 -->
    <div class="list-wrap content">

      <!-- 栏目操作区 -->
      <div class="menu-wrap">
        <el-input
          class="menu-input"
          placeholder="输入关键字搜索"
          size="small"
          v-model="filterText"
        >
        </el-input>
        <el-button
          v-if="login"
          class="add-btn"
          size="small"
          icon="el-icon-plus"
          @click="addCategory(0, 0)"
          >新增一级栏目</el-button
        >
        <el-tree
          class="filter-tree"
          :data="categoryData"
          :props="defaultProps"
          :filter-node-method="filterNode"
          ref="tree"
          @node-click="getNews"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span :title="node.label">{{ node.label }}</span>
            <span v-if="login">
              <i
                v-if="data.level == 0"
                class="menu-icon el-icon-circle-plus-outline"
                title="新增二级栏目"
                @click.stop="() => addCategory(1, data.id)"
              ></i>
              <i
                class="menu-icon el-icon-edit"
                title="编辑"
                @click.stop="() => updateCategory(data)"
              ></i>
              <i
                class="menu-icon el-icon-delete"
                title="删除"
                @click.stop="() => deleteCategort(data)"
              ></i>
            </span>
          </span>
        </el-tree>
      </div>
      <!-- / 栏目操作区 -->

      <!-- 新闻列表区 -->
      <div class="news-list">
        <div v-if="categoryPath.length == 2" class="list-header">
          <div>
            <el-breadcrumb separator-class="el-icon-arrow-right">
              <el-breadcrumb-item v-for="item in categoryPath" :key="item.id">
                {{ item.name }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div>
            <el-button
              v-if="login"
              type="primary"
              size="small"
              icon="el-icon-plus"
              @click="showAddNews"
              >新增新闻</el-button
            >
          </div>
        </div>
        <el-empty v-else description="请选择栏目"></el-empty>
        <el-empty v-if="categoryPath.length == 2 && newsList.length == 0" description="该栏目没有新闻"></el-empty>
        <ul class="list-box">
          <li
            class="list-item"
            v-for="item in newsList"
            :key="'list' + item.id"
          >
            <p class="item-title" :title="item.title" @click="viewNews(item)"> {{ item.title }} </p>
            <p class="item-content">{{ item.content | clearHtml }}</p>
            <p class="item-time">
              <span>最近编辑 · {{ item.updatedTime }} </span>
              <span v-if="login">
                <i
                  class="el-icon-edit-outline"
                  title="编辑新闻"
                  @click="showUpdateNews(item)"
                ></i>
                <i
                  class="el-icon-delete"
                  title="删除新闻"
                  @click="deleteNews(item)"
                ></i>
              </span>
            </p>
          </li>
          <el-pagination
            v-if="page.total > 0"
            class="page-box"
            background
            :total="page.total"
            @current-change="newsPageChange">
          </el-pagination>
        </ul>
      </div>
      <!-- / 新闻列表区 -->

      <!-- 新闻弹窗 -->
      <el-dialog
        class="news-dialog"
        :title="dialogTitle"
        :visible.sync="editNewsVisible"
        width="60%"
        @closed="closeEdit"
      >

        <p class="view-info">
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item v-for="item in categoryPath" :key="item.id">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <span> {{ newsData.updatedTime }} </span>
        </p>
        
        <!-- 编辑新闻区域 -->
        <div v-if="dialogTitle == '新增新闻' || dialogTitle == '编辑新闻'">
          <el-input
            class="edit-title"
            v-model="newsData.title"
            size="small"
            placeholder="请输入新闻标题"
          ></el-input>
          <quill-editor
            class="edit-content"
            :options="editorOption"
            v-model="newsData.content"
          ></quill-editor>
        </div>
        <!-- / 编辑新闻区域 -->

        <!-- 查看新闻区域 -->
        <div v-else class="view-main" v-html="newsData.content"></div>
        <!-- / 查看新闻区域 -->

        <span v-if="dialogTitle == '新增新闻' || dialogTitle == '编辑新闻'" slot="footer" class="dialog-footer">
          <el-button size="small" @click="editNewsVisible = false" :loading="loading"
            >取 消</el-button
          >
          <el-button v-if="dialogTitle == '新增新闻'" size="small" type="primary" @click="addNews" :loading="loading"
            >确 定</el-button
          >
          <el-button v-else size="small" type="primary" @click="saveNews" :loading="loading"
            >保 存</el-button
          >
        </span>
      </el-dialog>
      <!-- / 新闻弹窗 -->

    </div>
    <!-- / 主体区域 -->
  </div>
</template>

<script>
import cookie from "vue-cookies";
import { apiGet, apiPost } from "@/api/index";
export default {
  data() {
    return {
      login: false,
      userInfo: {},
      filterText: "",
      categoryData: [],
      defaultProps: {
        children: "childList",
        label: "name",
      },
      categoryPath: [],
      newsList: [],
      editNewsVisible: false,
      dialogTitle: "",
      newsData: {
        title: "",
        content: "",
      },
      // 富文本框配置
      editorOption: {
        modules: {
          toolbar: [
            ["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线
            [{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表
            [{ script: "sub" }, { script: "super" }], // 上标/下标
            [{ indent: "-1" }, { indent: "+1" }], // 缩进
            [{ size: ["small", false, "large", "huge"] }], // 字体大小
            [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
            [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
            [{ align: [] }], // 对齐方式
          ],
        },
      },
      // 分页配置
      page: {
        page: 1,
        limit: 10,
        total: 0
      },
      loading: false
    };
  },
  created() {
    this.getCategory();

    // 获取登录状态
    this.login =
      cookie.get("newsToken") || sessionStorage.newsToken ? true : false;
    
    if (this.login) this.getUserInfo();
  },
  methods: {
    // 获取用户信息
    async getUserInfo() {
      let res = await apiGet("renren-fast/app/userInfo");
      if (res.data.code == 0) this.userInfo = res.data.user;
    },
    // 退出登录
    logout() {
      this.$confirm("此操作将退出登录, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          // 清除用户 token
          sessionStorage.removeItem("newsToken");
          this.login = false;
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作",
          });
        });
    },

    // 获取栏目数据
    async getCategory() {
      let res = await apiGet("renren-fast/app/category/tree");
      if (res.data.code == 0) this.categoryData = res.data.data;
    },

    // 新增栏目
    addCategory(level, parentId) {
      this.$prompt("请输入栏目名", `新增${level ? "二" : "一"}级栏目`, {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(async ({ value }) => {
          let res = await apiPost("renren-fast/app/category/save", {
            name: value,
            parentId,
            level,
          });
          if (res.data.code == 0) {
            this.$message.success("操作成功");
            this.getCategory();
          }
        })
        .catch(() => {
          this.$message.info("取消操作");
        });
    },

    // 更新栏目
    updateCategory(data) {
      this.$prompt("请输入栏目名", `新增${data.level ? "二" : "一"}级栏目`, {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputValue: data.name,
      })
        .then(async ({ value }) => {
          let res = await apiPost("renren-fast/app/category/update", {
            id: data.id,
            name: value,
          });
          if (res.data.code == 0) {
            this.$message.success("操作成功");
            this.getCategory();
          }
        })
        .catch(() => {
          this.$message.info("取消操作");
        });
    },

    // 删除栏目
    deleteCategort(data) {
      this.$confirm("此操作将永久删除该栏目, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          let res = await apiPost("renren-fast/app/category/delete", [data.id]);
          if (res.data.code == 0) {
            this.$message.success("操作成功");
            this.getCategory();
          }
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },

    // 获取新闻数据
    async getNews(data) {
      if (data) {
        if (data.level == 0) return;
        this.categoryPath = [
          {
            id: data.parentId,
            name: this.categoryData.find((item) => item.id == data.parentId)
              .name,
          },
          {
            id: data.id,
            name: data.name,
          },
        ];
      }
      let res = await apiGet("renren-fast/app/article/pageList", {
        page: this.page.page,
        limit: this.page.limit,
        parentId: this.categoryPath[1].id,
      });
      if (res.data.code == 0) {
        this.newsList = res.data.page.list;
        this.page.total = res.data.page.totalCount;
      }
    },

    // 新闻分页
    newsPageChange(val) {
      this.page.page = val;
      this.getNews();
    },

    // 查看新闻详情
    async viewNews(data) {
      this.newsData = data;
      this.dialogTitle = data.title;
      this.editNewsVisible = true;
    },

    // 显示新增弹窗
    showAddNews() {
      this.dialogTitle = "新增新闻";
      this.editNewsVisible = true;
    },

    // 添加新闻
    async addNews() {
      if (!this.newsData.title) {
        this.$message.warning("新闻标题不能为空");
        return
      }
      this.loading = true;
      let res = await apiPost("renren-fast/app/article/save", {
        title: this.newsData.title,
        content: this.newsData.content,
        parentId: this.categoryPath[1].id,
      });
      if (res.data.code == 0) {
        this.$message.success("操作成功");
        this.getNews();
        this.editNewsVisible = false;
      }
      this.loading = false;
    },

    // 显示新闻编辑弹窗
    showUpdateNews(data) {
      this.dialogTitle = "编辑新闻";
      this.newsData = JSON.parse(JSON.stringify(data));
      this.editNewsVisible = true;
    },

    // 保存新闻编辑内容
    async saveNews() {
      if (!this.newsData.title) {
        this.$message.warning("新闻标题不能为空");
        return
      }
      this.loading = true;
      let res = await apiPost("renren-fast/app/article/update", {
        id: this.newsData.id,
        title: this.newsData.title,
        content: this.newsData.content,
        parentId: this.newsData.parentId,
      });
      if (res.data.code == 0) {
        this.$message.success("操作成功");
        this.getNews();
        this.editNewsVisible = false;
      }
      this.loading = false;
    },

    // 关闭弹窗清空数据
    closeEdit() {
      this.newsData = {};
    },

    // 删除新闻
    deleteNews(data) {
      this.$confirm("此操作将永久删除该新闻, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          let res = await apiPost("renren-fast/app/article/delete", [data.id]);
          if (res.data.code == 0) {
            this.$message.success("操作成功");
            this.getNews();
          }
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },

    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  filters: {
    // 清除 HTML 标签
    clearHtml(val) {
      return val.replace(/<[^>]+>/g, "");
    }
  }
};
</script>

<style lang="scss" scoped>
.home-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  & > header {
    flex: none;
    height: 50px;
    background: #ffffff;
    margin-bottom: 12px;
    box-shadow: 0 0 6px rgba($color: #000000, $alpha: 0.1);
    & > .header-wrap {
      height: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      & > .web-title {
        font-size: 24px;
      }
      & > .userinfo {
        display: flex;
        align-items: center;
        & > .info-box {
          font-size: 14px;
          color: #666;
          margin-right: 12px;
          & > span {
            margin-left: 10px;
          }
        }
        & > .logout {
          color: #409eff;
          cursor: pointer;
        }
      }
    }
  }
  & > .list-wrap {
    flex: 1;
    display: flex;
    overflow: hidden;
    padding-bottom: 12px;
    & > .menu-wrap {
      flex: none;
      width: 460px;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 5px;
      padding: 24px;
      margin-right: 12px;
      & > .menu-input {
        margin-bottom: 6px;
      }
      & > .add-btn {
        display: inline-block;
        margin-bottom: 6px;
      }
      & > .filter-tree {
        flex: 1;
        overflow-y: auto;
        border: 1px solid #eee;
        border-radius: 5px;
        padding: 6px;
        .custom-tree-node {
          width: 100%;
          display: flex;
          justify-content: space-between;
          overflow: hidden;
          & > span:nth-child(1) {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          .menu-icon {
            color: #409eff;
            margin-left: 12px;
          }
        }
      }
    }
    & > .news-list {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 5px;
      padding: 24px;
      overflow: hidden;
      & > .list-header {
        flex: none;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #eee;
        padding-bottom: 12px;
        margin-bottom: 12px;
      }
      & > .list-box {
        flex: 1;
        overflow-y: auto;
        & > .list-item {
          border-bottom: 1px solid #eee;
          padding: 12px;
          & > .item-title {
            display: block;
            font-size: 20px;
            color: #1e1e1e;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            margin-bottom: 8px;
            cursor: pointer;
            &:hover {
              color: #409eff;
              text-decoration: underline;
            }
          }
          & > .item-content {
            font-size: 16px;
            color: #666;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
            margin-bottom: 12px;
          }
          & > .item-time {
            text-align: right;
            font-size: 14px;
            color: #aaa;
            display: flex;
            justify-content: space-between;
            & > span:nth-child(2) {
              & > i {
                font-size: 18px;
                color: #409eff;
                cursor: pointer;
                margin-right: 10px;
              }
            }
          }
        }
        & > .page-box {
          padding: 12px 0;
          text-align: center;
        }
      }
    }
  }
}

.news-dialog {
  .edit-title {
    margin-bottom: 12px;
  }
  .edit-content {
    &::v-deep .ql-container {
      height: 200px;
    }
  }
  .view-info {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #eee;
    padding-bottom: 12px;
    margin-bottom: 12px;
    color: #666;
    font-size: 14px;
  }
  .view-main {
    padding: 12px;
  }
}
</style>
