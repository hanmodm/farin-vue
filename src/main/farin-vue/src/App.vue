<script setup>
  import { RouterLink, RouterView } from 'vue-router'
  import HelloWorld from './components/HelloWorld.vue'
  import LoadingOverlay from '@/components/LoadingOverlay.vue'
</script>

<template>
  <loading-overlay ref="loading"/>
  <header>
    <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
    <div class="wrapper">
      <!-- mount되는 시점에 instance가 생성 안되어 global properties에 전달이 안됨. 그래서 commUtil대신 t사용 -->
      <v-select label="Locale" v-model="locale" :items="locales" @update:modelValue="changeLocale"></v-select>
      <HelloWorld :msg="$t('msg.ctn.youdidit')" />

      <nav class="ml-2">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>
      <nav class="ml-2">
        <RouterLink to="/ajaxTest">{{ $t("lbl.menu.ajaxTest") }}</RouterLink>
        <RouterLink to="/stateTest">{{ $t("lbl.menu.stateTest") }}</RouterLink>
        <RouterLink to="/tcpIpServerTest">{{ $t("lbl.menu.tcpIpServerTest") }}</RouterLink>
        <RouterLink to="/tcpIpSendTest">{{ $t("lbl.menu.tcpIpSendTest") }}</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView @open-loading="screenOpenLoading" @close-loading="screenCloseLoading"/>
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    /*padding: 1rem 0;*/
    margin-top: 1rem;
  }
}
</style>
<script>
export default {
  components: {
    LoadingOverlay
  },
  data() {
    return {
      locale: "ko"
    }
  },
  computed: {
    locales() {
      return [ 
        { title: this.$t('lbl.etc.korean'), value: 'ko' },
        { title: this.$t('lbl.etc.english'), value: 'en' },
        { title: this.$t('lbl.etc.chinese'), value: 'zh' } ]
    }
  },
  methods: {
    changeLocale() {
      this.$commUtil.changeLocale(this.locale)
    },
    screenOpenLoading() {
      this.$refs.loading.openLoading()
    },
    screenCloseLoading() {
      this.$refs.loading.closeLoading()
    }
  }
}
</script>