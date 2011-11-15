/**pclock - a scalable html5 polar clock.
@author Benjamin Ferrari
@mail a9bejo@gmail.com

License:
========
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

function loadClock(canvasId){
    setInterval(drawClock,50,canvasId);
}

function drawClock(canvasId){
    var items = {
        "month" : {
            "percent" :  function(time){return ((time.getDate()/daysInMonth(time.getMonth(), time.getYear()) + time.getMonth())/12);},
            "unit" : function(time){return ["January","February","March","April","May","June","July","August","September","October","November","December"][time.getMonth()]},
            "color" : "#aa0044"
        },
        "days" : {
            "percent" :  function(time){
                return ((time.getHours()/24)+time.getDate()) / daysInMonth(time.getMonth(), time.getYear());
            },
            "unit" : function(time){return time.getDate() + "nd"},
            "color" : "#cc3333"},
        "weekday" : {
            "percent" :  function(time){
                return ((time.getHours()/24)+time.getDay()) / 7;
            },
            "unit" : function(time){return {0:"Sunday", 1:"Monday",2:"Thuesday",3:"Wednesday",4:"Thursday",5:"Friday",6:"Saturday"}[time.getDay()] },
            "color" : "#ffcc00"},
        "hours" : {
            "percent" :  function(time){return ((time.getMinutes()/60)+time.getHours())/24;},
            "unit" : function(time){return time.getHours() + " hours"},
            "color" : "#ff6666"},
        "minutes" : {
            "percent" :  function(time){return ((time.getSeconds()/60)+time.getMinutes())/60;},
            "unit" : function(time){return time.getMinutes() + " minutes"},
            "color" : "#0066ff"},
        "seconds" : {
            "percent" :  function(time){return ((time.getMilliseconds()/1000.0)+time.getSeconds())/60;},
            "unit" : function(time){return time.getSeconds() + " seconds"},
            "color" : "#00aa55"}
    };

    var canvas = document.getElementById(canvasId);
    var width = canvas.width;
    var height = canvas.height;
    if(canvas.getContext){       
        var ctx = canvas.getContext("2d");        
        ctx.save();
        ctx.clearRect(0,0,width,height);
        ctx.translate(width/2,height/2);
        ctx.scale(1,1);
        ctx.lineWidth   = width/16;
        ctx.lineCap = "square";
        var  time = new Date();
        drawText(ctx, time.getFullYear(), (width/25)+"pt Arial",-(width/16),(width/80),0);
        ctx.rotate(-Math.PI/2);
        var radius = width/11;
        var stepwidth = 1.2 * ctx.lineWidth; 
        for(var name in items){
            var item = items[name];
            this.drawTimeLine(ctx,radius,name, item, width,time);
            radius = radius + stepwidth;
        }
        ctx.restore();
    }
}

function drawText(ctx, text, font, x,y, angle){
    ctx.rotate(angle);
    ctx.font = font;
    ctx.fillText(text, x,y); 
    ctx.rotate(-angle);
}

function daysInMonth(month,year) {
    var m = [31,28,31,30,31,30,31,31,30,31,30,31];
    if (month != 2){ return m[month - 1];}
    if (year%4 != 0){ return m[1];}
    if (year%100 == 0 && year%400 != 0){ return m[1];}
    return m[1] + 1;
};

function initializeContext(canvas){
    var ctx = canvas.getContext("2d");
    ctx.save();
    ctx.translate(200,200);
    ctx.scale(1,1);
    ctx.lineWidth   = 25;
    ctx.lineCap = "round";
    ctx.clearRect(0,0,canvas.width,canvas.height);
    return ctx;
}

function drawTimeLine(ctx,radius,name, item,size, time){
    ctx.save();
    ctx.strokeStyle = item.color;
    ctx.beginPath();
    var x=0, y=0, startAngle=0, endAngle=2*Math.PI * item.percent(time);
    var anticlockwise=false;
    ctx.arc(x,y,radius,startAngle, endAngle, anticlockwise);  
    ctx.moveTo(0,0);
    ctx.stroke();
    var label = item.unit(time);
    drawText(ctx,label,(size/50)+"pt Arial",-(size/20),-radius+(size/80),(Math.PI/1.75));
    ctx.restore();
}

