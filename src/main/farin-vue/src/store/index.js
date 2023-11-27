import { createStore, createLogger } from 'vuex'
import MParam from './modules/MParam.js'

const debug = process.env.NODE_ENV !== 'production'
export default createStore({
    strict: debug,
    plugins: debug ? [createLogger()] : [],
    modules: {
        'param': MParam
    }
})