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
    let movieInfo = "<p>Movie Title: " + resultData[1]["title"] + "</p>" + "<p>Year: " + resultData[1]["year"]
        + "</p>" + "<p>Director: " + resultData[1]["director"] + "</p>"
         + "<p>Movie Rating: " + resultData[1]["rating"] + "</p>";
    const myArray1 = resultData[1]["genres"].split(",");
    movieInfo += "<p>Genres: ";
    for (let i = 0; i < myArray1.length;++i)
    {
       movieInfo+='<a href="BrowseMovie.html?sorted=tara&load_size=10&offset=0&genre=' + myArray1[i] + '">' + myArray1[i] + '</a>'+ "   ";
    }
    movieInfo += "</p>"

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append(movieInfo);

    console.log("handleResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#star_table_body");
    let backElement = jQuery("#back");
    let backHTML = '<a href=' + resultData[0]["back"] + '>'
        + "Go Back" +
        '</a>';
    console.log("back appeneded");
    backElement.append(backHTML);
    // Concatenate the html tags with resultData jsonObject to create table rows

    let rowHTML = "";
    rowHTML += "<tr>";
    const myArray = resultData[1]["stars"].split(",");
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