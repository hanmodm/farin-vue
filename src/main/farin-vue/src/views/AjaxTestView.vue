<template>
    <v-container >
        <v-row align="center" no-gutters>
            <v-col>
                <v-sheet>
                    <v-text-field v-ripple :label="$commUtil.getMsg('lbl.etc.data1')" readonly v-model="myId"></v-text-field>
                    <v-text-field v-ripple :label="$commUtil.getMsg('lbl.etc.data2')" readonly v-model="myNm"></v-text-field>
                    <v-text-field v-ripple :label="$commUtil.getMsg('lbl.etc.data3')" readonly v-model="myStateTest"></v-text-field>
                </v-sheet>
            </v-col>
            <v-col cols="3">
                    <v-btn class="text-none ml-5 mb-5" density="default" @click="getTestData">{{ $commUtil.getMsg("lbl.btn.viewServer") }}</v-btn>
                    <v-btn class="text-none ml-5 mb-5" density="default" @click="refresh">{{ $commUtil.getMsg("lbl.btn.changeState") }}</v-btn>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>
    const View = {
        data() {
            return {
                myId: "-",
                myNm: "-",
                myStateTest: "-"
            }
        },
        mounted() {
            this.myStateTest = this.$store.getters['param/getFirstData']?.id || '-'
        },
        methods: {
            getTestData() {
                let that = this

                //this.$refs.loading.openLoading()
                that.$emit('open-loading')
                that.$axios.get("/svcApi/test?id=1")
                .then((d) => {
                    that.myId = d.data["ID"]
                    that.myNm = d.data["NM"]
                    //that.$commUtil.showMsg("msg.com.recievedData")
                })
                .catch(e => {
                    alert(that.$commUtil.getMsg("msg.com.systemErr"))
                })
                .finally(() => {
                    //that.$refs.loading.closeLoading()
                    that.$emit('close-loading')
                })
            },
            refresh() {
                let id = (new Date()).toISOString()
                // action 호출
                this.$store.dispatch('param/replaceData', { id, idx: 0 })
                
                this.myStateTest = this.$store.getters['param/getFirstData']?.id
            }
        }
    }

    export default View;
</script>
