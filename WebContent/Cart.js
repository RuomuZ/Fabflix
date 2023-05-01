function handleCart(resultData) {
    let item_list = $("#item_list");
    let total = resultData["total"];
    let resultArray = resultData["previousItems"];
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
    let totalElement = jQuery("#total");
    totalElement.append(total);
    let backElement = jQuery("#back");
    let backHTML = '<a href=' + resultData["back"] + '>'
        + "Go Back" +
        '</a>';
    console.log("back appeneded");
    backElement.append(backHTML);
}

jQuery.ajax("api/cart", {
    method: "POST",
    success: resultDataString => {
        let resultDataJson = JSON.parse(resultDataString);
        handleCart(resultDataJson);
    }
});