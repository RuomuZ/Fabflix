/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log("here");
    console.log(resultData.length);
    // Iterate through resultData, no more than 10 entriess
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        console.log("ite");
        let rowHTML = "";
        rowHTML += "<tr>";
        /*
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        */
            rowHTML += "<th>" + '<a href="single-movie.html?title=' + resultData[i]["movie_title"] + '">'
                + resultData[i]["movie_title"] +     // display star_name for the link text
                '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        const myArray = resultData[i]["movie_stars"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < myArray.length; i++)
        {
            rowHTML += '<a href="single-star.html?name=' + myArray[i] + '">'
                + myArray[i] +     // display star_name for the link text
                '</a>' + "||";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        console.log("done");
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/topMovieList", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});