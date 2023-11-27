import './assets/main.css'

import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'
import store from '@/store'
import { i18n } from '@/i18n.js'
import CommUtil from '@/components/commutil.js'
import axios from 'axios'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({
    components,
    directives
  })

const app = createApp(App)

app.use(router).use(i18n).use(store).use(vuetify)

app.config.globalProperties.$axios = axios
app.config.globalProperties.$commUtil = new CommUtil(app)

app.mount('#app')