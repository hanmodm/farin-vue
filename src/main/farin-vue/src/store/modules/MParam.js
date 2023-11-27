const state = {
    params: []
}

const getters = {
    isEmpty: (s) => s.params.length === 0,
    getData: (s,gs,root) => (idx) => {
        return s.params.filter((n, i) => i === idx)[0]
    },
    getFirstData: (s,gs,root) => {
        return gs.getData(0)
    }
}

const actions = {
    pushData({state, commit, gettrs }, { id }) {
        commit('commitPushData', { id, value })
    },
    replaceData({ state, commit, gettrs }, { id, value, idx }) {
        commit('commitPushData', { id, value, idx })
    }
}

const mutations = {
    commitPushData(state, { id, value = 0, idx = 0}) {
        if (state.params.length > idx) {
            state.params[idx] = { id, value }
        } else {
            state.params.push({ id, value })
        }
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}