function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    starInfoElement.append("<p>Star Name: " + resultData[1]["star_name"] + "</p>" + "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");
    console.log("handleResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");
    let backElement = jQuery("#back");
    let backHTML = '<a href=' + resultData[0]["back"] + '>'
        + "Go Back" +
        '</a>';
    console.log("back appeneded");
    backElement.append(backHTML);
    let rowHTML = "";
    rowHTML += "<tr>";
    const myArray = resultData[1]["movie_title"].split(",");
    rowHTML += "<th>";
    console.log("here");
    for (let i = 0; i < myArray.length; i++) {
        rowHTML += '<a href="single-movie.html?title=' + myArray[i] + '">'
            + myArray[i] +     // display star_name for the link text
            '</a>' + "||";
    }
    rowHTML += "</th>";
    rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starName = getParameterByName('name');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?name=" + starName, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});