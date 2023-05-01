let backElement = jQuery("#back");
let backHTML = '<a href=' + resultData["back"] + '>'
    + "Go Back" +
    '</a>';
console.log("back appeneded");
backElement.append(backHTML);