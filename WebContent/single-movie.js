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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Title: " + resultData[0]["title"] + "</p>" + "<p>Year: " + resultData[0]["year"]
        + "</p>" + "<p>Director: " + resultData[0]["director"] + "</p>"
        + "<p>Genres: " + resultData[0]["genres"] + "</p>"
    );
    /*
    if (resultData[0]["star_dob"].localeCompare("null") == 0)
    {
        starInfoElement.append("<p>Date Of Birth: " + "N/A" + "</p>");
    }
    else
    {
        starInfoElement.append("<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");
    }
    */
    console.log("handleResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        const myArray = resultData[i]["stars"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < myArray.length; i++)
        {
            rowHTML += '<a href="single-star.html?name=' + myArray[i] + '">'
                + myArray[i] +     // display star_name for the link text
                '</a>' + "||";
        }
        rowHTML += "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieTitle = getParameterByName('title');
console.log(movieTitle);
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?title=" + movieTitle, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});