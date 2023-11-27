<template>
    <v-overlay :model-value="_overlay" :class="{ 'align-center justify-center': true, 'loading-wait': _overlay }">
        <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
    </v-overlay>
</template>
<script>
export default {
    data() {
        return {
            _overlay: false
        }
    },
    methods: {
        async openLoading() {
            let that = this
            that._overlay = true
        },
        async closeLoading() {
            let that = this
            that._overlay = false
        }
    }
}
</script>

<style>
    .loading-wait { cursor:wait; }
</style>