(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d0d2f64"],{"5b78":function(t,e,n){"use strict";n.r(e);var a=function(){var t=this,e=t._self._c;return e("div",{attrs:{id:"crea-box"}},[e("el-input",{attrs:{type:"textarea",size:"medium",autosize:"{ minRows: 2, maxRows: 10 }"},model:{value:t.text,callback:function(e){t.text=e},expression:"text"}},[t._v(t._s(t.text))]),e("el-button",{attrs:{type:"button"},on:{click:function(e){return t.next()}}},[t._v(" 下一条 ")]),e("el-button",{attrs:{type:"button"},on:{click:function(e){return t.add()}}},[t._v(" 添加 ")])],1)},i=[],o=n("4ec3"),s={name:"crea",data(){return{text:"默认显示"}},created(){this.next()},methods:{async next(){let t=await o["a"].creatCaihongp();this.text=t},add(){o["a"].addEntity(this.text)}}},c=s,r=n("2877"),u=Object(r["a"])(c,a,i,!1,null,"ee4a0b50",null);e["default"]=u.exports}}]);
//# sourceMappingURL=chunk-2d0d2f64.6d89fae2.js.map