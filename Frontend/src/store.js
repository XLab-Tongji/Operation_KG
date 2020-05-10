import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    fir:{},
    data: [],
    date:[]
  },
  mutations: {
    setFir(state, value) {
      state.fir = value;
    },
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
