<template>
    <v-container >
        <v-row align="center" no-gutters>
            <v-col>
                <v-sheet>
                    <v-text-field v-ripple :label="$commUtil.getMsg('lbl.etc.savedStateVal')" readonly v-model="myStateTest"></v-text-field>
                </v-sheet>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>
    export default {
        data() {
            return {
                myStateTest: "-"
            }
        },
        mounted() {
            this.getStateFirstData()
        },
        methods: {
            getStateFirstData() {
                this.myStateTest = this.$store.getters['param/getFirstData']?.id
            }
        }
    }
</script>
