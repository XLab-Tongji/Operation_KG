import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    fir:{},
    data: [],
    date:[],
    trans:"",
    pat:""
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
    },
    setTrans(state, value) {
      state.trans = value;
    },
    setPat(state, value) {
      state.pat = value;
    }
  },
  actions: {

  },
  plugins: [createPersistedState()],
})
