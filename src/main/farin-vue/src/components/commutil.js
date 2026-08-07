import { loadLocale } from '@/i18n'

class CommUtil {
    app = null
    constructor(vueApp) {
        this.app = vueApp
    }
    showMsg(msg, args) {
        if (!this.app || !this.app._instance) {
            alert(msg)
        } else {
            //alert("Vue error message :: [" + msg + "]")
            alert(this.app._instance.ctx.$t(msg, args))
        }
    }
    getMsg (code, args, defaultMsg) {
        if (!this.app || !this.app._instance) return !!defaultMsg ? defaultMsg : ""
        return this.app._instance.ctx.$tc(code, args)
    }
    isEmpty(o, zeroIsEmpty) {
        if (!o) {
            return (typeof o === "number" && zeroIsEmpty) || typeof o !== "number" ? true : false
        } else if (typeof o === "object") {
            if (!!o.length) {
                return o.length === 0 ? true : false
            } else {
                return Object.keys(o).length === 0 && o.constructor === Object
            }
        } else {
            return false
        }
    }
    async changeLocale(lc='ko') {
        /*if (!this.app || !this.app._instance) return
        let $lc = this.app._instance.ctx.$i18n.locale
        this.app._instance.ctx.$i18n.locale = !lc ? $lc : lc*/
        await loadLocale(lc)
    }
}

export default CommUtil