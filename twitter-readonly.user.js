// ==UserScript==
// @name          twitter-readonly
// @autor         Benjamin Ferrari
// @namespace     http://bookworm.at
// @description   removes the status update form from twitter.com. 
//                Useful if you want to update via ping.fm or identi.ca 
//                but have a habit of using this form.
// @include       http://twitter.com/home
// ==/UserScript==

document.getElementById("status").style.display="none";