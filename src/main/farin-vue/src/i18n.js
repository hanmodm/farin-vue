import { createI18n } from 'vue-i18n'

export const i18n = new createI18n({
    locale: 'ko',
    fallbackLocale: 'ko',
    messages: {}
})

export const loadLocale = async (locale) => {
    if (i18n.global.availableLocales.includes(locale)) {
        i18n.global.locale = locale
        return
    }
    const module = await import(`@/locales/${locale}.json`)
    i18n.global.setLocaleMessage(locale, module.default)
    i18n.global.locale = locale
}
