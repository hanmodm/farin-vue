import { createI18n } from 'vue-i18n'
import ko from '@/locales/ko.json'
import en from '@/locales/en.json'
import zh from '@/locales/zh.json'

export const i18n = new createI18n({
    locale: 'ko',
    fallbackLocale: 'ko',
    messages: {
        ko, en, zh
    }
})