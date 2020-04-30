import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    data: [],
    date:[]
  },
  mutations: {
    setData(state, value) {
      state.data = value;
    },
    setDate(state, value) {
      state.date = value;
    }
  },
  actions: {

  },
  plugins: [createPersistedState()],
})
