const HOST = "https://49.232.141.126:8080"
const GET_CONFERENCE = HOST + "/api/user/establishes/"
const LOGIN = HOST + "/api/login"



// const keyt = [113, -49, 55, -11, -57, -3, 125, -26]
const keyt = "6eGicG6U"
const ivt = [-38, -31, -25, 123, 109, -82, -68, -45]

const key = CryptoJS.enc.Utf8.parse(keyt); // key
const iv = CryptoJS.enc.Utf8.parse(byteToString(ivt)); // iv


// 直接使用 key 是不对的,需要像上面那样处理
// const key  = "4c43c365a4ac05b91eb5fa95"; // key
// const iv = key.substr(0, 8); // iv

function datetimeFormat(longTypeDate) {
  var date = new Date(longTypeDate);
  var y = date.getFullYear();
  var m = date.getMonth() + 1;
  m = m < 10 ? ('0' + m) : m;
  var d = date.getDate();
  d = d < 10 ? ('0' + d) : d;
  var h = date.getHours();
  h = h < 10 ? ('0' + h) : h;
  var minute = date.getMinutes();
  var second = date.getSeconds();
  minute = minute < 10 ? ('0' + minute) : minute;
  second = second < 10 ? ('0' + second) : second;
  return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}

function stringToByte(str) {
  var bytes = new Array();
  var len, c;
  len = str.length;
  for (var i = 0; i < len; i++) {
    c = str.charCodeAt(i);
    if (c >= 0x010000 && c <= 0x10FFFF) {
      bytes.push(((c >> 18) & 0x07) | 0xF0);
      bytes.push(((c >> 12) & 0x3F) | 0x80);
      bytes.push(((c >> 6) & 0x3F) | 0x80);
      bytes.push((c & 0x3F) | 0x80);
    } else if (c >= 0x000800 && c <= 0x00FFFF) {
      bytes.push(((c >> 12) & 0x0F) | 0xE0);
      bytes.push(((c >> 6) & 0x3F) | 0x80);
      bytes.push((c & 0x3F) | 0x80);
    } else if (c >= 0x000080 && c <= 0x0007FF) {
      bytes.push(((c >> 6) & 0x1F) | 0xC0);
      bytes.push((c & 0x3F) | 0x80);
    } else {
      bytes.push(c & 0xFF);
    }
  }
  return bytes;
}

function encrypted(params) {
  const encrypted = CryptoJS.DES.encrypt(params, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  });
  console.log("加密加蜜加米", params, stringToByte(encrypted.ciphertext.toString()), stringToByte(encrypted.ciphertext.toString(CryptoJS.enc.Base64)))
  return encrypted.ciphertext.toString(CryptoJS.enc.Base64); // 返回加密后的字符串
}



function decrypted(params) {

  // const decrypted = CryptoJS.TripleDES.decrypt(params, key, {
  //   iv: iv,
  //   mode: CryptoJS.mode.CBC,
  //   padding: CryptoJS.pad.Pkcs7
  // }).toString(CryptoJS.enc.Utf8);

  var decrypted = CryptoJS.DES.decrypt({
    ciphertext: CryptoJS.enc.Hex.parse(params)
  }, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  }).toString(CryptoJS.enc.Utf8);

  return decrypted;

  return decrypted // 返回解密后的字符串
}

// const encryptkey = "cc839cf9feba4ed7ba68064177a0b505"

// const hah = CryptoJS.enc.Base64.parse(encryptkey);
// console.log("Encode: ", CryptoJS.enc.Utf8.parse("123456"))
// const bala = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse("123456"))
// console.log(CryptoJS.enc.Base64.parse(bala))
// console.log(hah.toString(CryptoJS.enc.Utf8))
// console.log(hah)

function byteToString(arr) {
  if (typeof arr === 'string') {
    return arr;
  }
  var str = '',
    _arr = arr;
  for (var i = 0; i < _arr.length; i++) {
    var one = _arr[i].toString(2),
      v = one.match(/^1+?(?=0)/);
    if (v && one.length == 8) {
      var bytesLength = v[0].length;
      var store = _arr[i].toString(2).slice(7 - bytesLength);
      for (var st = 1; st < bytesLength; st++) {
        store += _arr[st + i].toString(2).slice(2);
      }
      str += String.fromCharCode(parseInt(store, 2));
      i += bytesLength - 1;
    } else {
      str += String.fromCharCode(_arr[i]);
    }
  }
  return str;
}

// console.log("ahh", byteToString(keyt))

// let xx = encrypted("123456")
// console.log("猫出一个光头！！！！！！！！", xx)
// console.log("这是前端的加蜜！", CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(encrypted("123456"))))
// console.log("I'm FINE!")
// let tt = "I'm FINE!"
// console.log(tt.toString(CryptoJS.enc.Utf8))
// let mm = decrypted(xx)
// console.log(mm)

function login() {
  formi = document.getElementById('form')
  reqdata = new FormData(formi)
  // let formdata = new FormData()
  // formdata.append("username", "user1")
  // formdata.append("password", encrypted("123456"))
  console.log(reqdata)
  let urlencoded = new URLSearchParams();
  urlencoded.append("username", reqdata.get('username'));
  urlencoded.append("type", "1");
  urlencoded.append("password", reqdata.get('password'));
  console.log(urlencoded)
  let req = new Request(LOGIN, {
    method: "POST",
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json',
    },
    body: urlencoded
  })
  fetch(req).then(resp => {
    console.log(resp.headers)
    console.log("请求是：", req)
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "conference.html"
  }).catch(e => alert(e))
}

function getConference() {
  console.log(document.cookie)
  fetch(HOST + "/api/user/establishes?id=4", {
    credentials: 'include',
    headers: {
      'Content-Type': "application/x-www-form-urlencoded"
    }
  })
  .then(resp => resp.json()).then(_data => {
    console.log(_data)
    console.log(_data["conferences"])
    console.log(_data["conference_num"])
    let data = _data["conferences"]
    data.forEach(dt => dt["date"] = datetimeFormat(dt["date"]).split(' ')[0])
    let container = document.querySelector('#container');
    //也就是获取我们定义的模板的dom对象。主要是想获取里面的内容（innerHTML）
    let templateDom = document.querySelector('#template');
    //编译模板的里的内容
    let template = Handlebars.compile(templateDom.innerHTML);
    //把后台获取到的数据student渲染到页面
    container.innerHTML = template(data);

    let props = ["name", "chairs", "date", "introduction", "organization", "place"]
    console.log("propssss!!!!!", props)
    props.forEach(element => {
      console.log('.modal-update-'+element)
      let doms = document.querySelectorAll('.modal-update-'+element)
      console.log("DOOOOOOOOMS", doms)
      doms.forEach(item => {
        let id = item.id.split('-').slice(-1)[0]
        UIkit.util.on('#'+item.id, 'click', function (e) {
          e.preventDefault();
          e.target.blur();
          console.log("ITEM!!!", item.id, element)
          UIkit.modal.prompt(element, '').then(name => {
            if (name != null)
              updateConference(id, element, name)
          })
        })
      })
    })
  }).catch(e => alert(e))
}

// window.addEventListener('unload', login())

function render() {
  //   let url = GET_CONFERENCE + '?id=4'
  //   // let url = 'http://p3.pstatp.com/large/82000033089aadd89f4'
  //   let req = new Request(url, {
  //     method: "GET",
  //     headers: {
  //       'Content-Type': 'application/json',
  //       'cookie': document.cookie
  //     },
  //     // body: JSON.stringify({
  //     //   day: '2020-05-26'
  //     // })
  //   })
  // //   var container = document.querySelector('#container');
  // //   //也就是获取我们定义的模板的dom对象。主要是想获取里面的内容（innerHTML）
  // //   var templateDom = document.querySelector('#template');
  // //   //编译模板的里的内容
  // //   var template = Handlebars.compile(templateDom.innerHTML);
  //   //把后台获取到的数据student渲染到页面
  //   fetch(req).then(resp => {
  //     return resp.json()
  //   }).then(data => {
  //     document.write(JSON.stringify(data))
  //   }).catch(e => { 
  //     document.write(e.toString())
  //   })
}

function hreftoSession(id) {
  window.localStorage.setItem("conference_id", id)
  window.location.href = "session.html"
}

function getSessions() {
  let conference_id = window.localStorage.getItem("conference_id")
  console.log(conference_id)
  fetch(HOST + "/api/sessions/foradmin?conference_id=" + conference_id, {
    credentials: 'include',
    headers: {
      'Content-Type': "application/x-www-form-urlencoded"
    }
  })
  .then(resp => resp.json()).then(_data => {
    let data = _data['sessions']
    data.forEach(dt => dt["start_time"] = datetimeFormat(dt["start_time"]))
    data.forEach(dt => dt["end_time"] = datetimeFormat(dt["end_time"]))
    let container = document.querySelector('#container')
    //也就是获取我们定义的模板的dom对象。主要是想获取里面的内容（innerHTML）
    let templateDom = document.querySelector('#template');
    //编译模板的里的内容
    let template = Handlebars.compile(templateDom.innerHTML);
    //把后台获取到的数据student渲染到页面
    container.innerHTML = template(data);

    let props = ["name", "reporters", "start_time", "end_time", "tag", "topic"]
    props.forEach(element => {
      let doms = document.querySelectorAll('.modal-update-'+element)
      doms.forEach(item => {
        let id = item.id.split('-').slice(-1)[0]
        UIkit.util.on('#'+item.id, 'click', function (e) {
          e.preventDefault();
          e.target.blur();
          UIkit.modal.prompt(element, '').then(name => {
            if (name != null)
              updateSession(id, element, name)
          })
        })
      })
    })
  }).catch(e => alert(e))
}

// function getPapers(id) {
//   fetch(HOST + "/api/session/talks?session_id=" + id, {
//     credentials: 'include',
//     headers: {
//       'Content-Type': "application/x-www-form-urlencoded"
//     }
//   })
//     .then(resp => resp.json()).then(_data => {
//       let data = _data['papers']
//       let container = document.querySelector('#papercontainer')
//       //也就是获取我们定义的模板的dom对象。主要是想获取里面的内容（innerHTML）
//       let templateDom = document.querySelector('#papertemplate');
//       //编译模板的里的内容
//       let template = Handlebars.compile(templateDom.innerHTML);
//       //把后台获取到的数据student渲染到页面
//       container.innerHTML = template(data);
//     }).catch(e => alert(e))
// }

function createConference() {
  let formc = document.getElementById('newconf')
  let confdata = new FormData(formc)
  let objdata = {}
  confdata.forEach((value, key) => objdata[key] = value)
  let str = objdata['chairs'];
  objdata['chairs'] = str.split(',')
  objdata['visible'] = 1
  objdata['start_time'] = objdata['start_time'] + " 08:00:00"
  jsondata = JSON.stringify(objdata)

  console.log("confdata: ", confdata)
  console.log("objdata: ", objdata)
  console.log("jsondata: ", jsondata)
  let urlencoded = new URLSearchParams();
  urlencoded.append("type", "1");
  urlencoded.append("conference", jsondata);
  let req = new Request(HOST + "/api/user/establishing/conference", {
    method: "POST",
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json',
    },
    body: urlencoded
  })
  fetch(req).then(resp => {
    console.log(resp.headers)
    console.log("请求是：", req)
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "conference.html"
  }).catch(e => alert(e))
}

function createSession() {
  let forms = document.getElementById('newsess')
  let sessdata = new FormData(forms)
  let objdata = {}
  sessdata.forEach((value, key) => objdata[key] = value)
  objdata['start_time'] = objdata['start_time'].split('T').join(' ') + ":00"
  objdata['end_time'] = objdata['end_time'].split('T').join(' ') + ":00"
  objdata['visible'] = 1
  jsondata = JSON.stringify(objdata)
  console.log(jsondata)
  console.log(objdata)
  let urlencoded = new URLSearchParams();
  urlencoded.append("type", "1");
  urlencoded.append("session", jsondata);
  urlencoded.append("conference_id", window.localStorage.getItem("conference_id"))
  let req = new Request(HOST + "/api/user/establishing/sessions", {
    method: "POST",
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json',
    },
    body: urlencoded
  })
  fetch(req).then(resp => {
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "session.html"
  }).catch(e => alert(e))
}

function hreftoPaper(id) {
  window.localStorage.setItem("session_id", id)
  window.location.href = 'createpaper.html';
}

function addPaper() {
  let formp = document.getElementById('newpaper')
  let data = new FormData(formp)
  let reqdata = new FormData()
  console.log(data)
  let file = document.getElementById('paperfile')
  let paper = {}
  paper["title"] = data.get("title")
  paper["authors"] = data.get("authors")
  paper["abstr"] = data.get("abstr")
  reqdata.append("file", file.files[0])
  reqdata.append("paper", JSON.stringify(paper))
  reqdata.append("type", 1)
  reqdata.append("session_id", window.localStorage.getItem("session_id"))
  console.log(reqdata)
  let req = new Request(HOST + "/api/user/establishing/paper", {
    method: "POST",
    body: reqdata
  })
  fetch(req).then(resp => resp.text())
    .then(data => alert(data)).catch(e => alert(e))
  // window.location.href = "session.html"
}

function deleteConference(id) {
  fetch(HOST + "/api/user/deleting?conference_id=" + id).then(resp => {
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "session.html"
  }).catch(e => alert(e))
}

function deleteSession(id) {
  console.log("deleting session...")
  fetch(HOST + "/api/user/deleting/session?session_id=" + id).then(resp => {
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "session.html"
  }).catch(e => alert(e))
}

function deletePaper(id) {
  console.log("deleting paper..")
  fetch(HOST + "/api/user/deleting/paper?paper_id=" + id).then(resp => {
    return resp.text()
  }).then((data) => {
    alert(data)
    window.location.href = "session.html"
  }).catch(e => alert(e))
}

function updateConference(id, property, value) {
  fetch(HOST + "/api/conference/update?id="+id+"&property="+property+"&value="+value)
  .then(resp => resp.text()).then(data => {
    alert(data)
    window.location.reload()
  }).catch(e => alert(e))
}

function updateSession(id, property, value) {
  fetch(HOST + "/api/session/update?id="+id+"&property="+property+"&value="+value)
  .then(resp => resp.text()).then(data => {
    alert(data)
    window.location.reload()
  }).catch(e => alert(e))
}