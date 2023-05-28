let current = window.location.href;
let index = current.indexOf("BrowseMovie");
let t = current.slice(index,current.length);
to = "api/" + t;
let load = parseInt(getParameterByName("load_size"))
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

function update(form){
    let l = form.load_size.value;
    let s = form.sorted.value;
    let cl = getParameterByName("load_size")
    let cs = getParameterByName("sorted")
    let new_url = current.replace("sorted="+cs,"sorted="+s);
    new_url = new_url.replace("load_size="+cl, "load_size="+l);
    window.location.replace(new_url);
}

function handleAdd(movie_title) {
    $.ajax("api/addItem?item=" + movie_title, {
        method: "GET",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });
    // clear input form

}
function handleCartArray(rs)
{
}



function handleStarResult(resultData) {

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    // Iterate through resultData, no more than 10 entriess
    for (let i = 1; i < Math.min(load + 1, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?title=' + resultData[i]["movie_title"] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        const myArray1 = resultData[i]["movie_genres"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < myArray1.length; i++)
        {
            rowHTML += '<a href="BrowseMovie.html?sorted=tara&load_size=10&offset=0&genre=' + myArray1[i] + '">'
                + myArray1[i] +
                '</a>' + "||";
        }
        rowHTML += "</th>";
        if (resultData[i]["movie_stars"] != null) {
            const myArray = resultData[i]["movie_stars"].split(",");
            rowHTML += "<th>";
            for (let i = 0; i < myArray.length; i++) {
                rowHTML += '<a href="single-star.html?name=' + myArray[i] + '">'
                    + myArray[i] +     // display star_name for the link text
                    '</a>' + "||";
            }
            rowHTML += "</th>";
            rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

            rowHTML += "<th>";
        } else {rowHTML += "<th>Empty</th>>";}
        let movie_title = resultData[i]["movie_title"];
        rowHTML += "<button onClick='handleAdd(\"" + movie_title + "\")'>ADD</button>";


        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);


    }
    let PageElement = jQuery("#page");
    let num_rec = parseInt(resultData[0]["total"]);
    let num_page = Math.ceil(num_rec / load)
    let htap = ""
    let offset = parseInt(getParameterByName("offset"))
    let current_page = (offset / load) + 1
    if (current_page == 1)
    {
        htap += "<a href=\"#\">&laquo;</a>";
    }
    else{
        let o = offset - load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())
        htap += "<a href=" + p + ">&laquo;</a>";
    }

    for (let i = 1; i <= num_page; i++)
    {
        let o = offset + (i - current_page) * load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())

        if (i == current_page) {
            htap += '<a class = "active" href=' + p + '>'
                +i+'</a>';
        }
        else{
            htap += '<a href=' + p + '>'
                +i+'</a>';
        }
    }
    if (current_page == num_page)
    {
        htap += "<a href=\"#\">&raquo;</a>";
    }
    else{
        let o = offset + load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())
        htap += "<a href=" + p + ">&raquo;</a>";
    }
    PageElement.append(htap)
}



var char_set = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W"
    ,"X","Y","Z","0","1","2","3","4","5","6","7","8","9","*"];

function handleGenre(rs){
    let genresList = jQuery("#genreList");

    let genresAppend = "";
    for (let i = 0; i < rs.length; ++i)
    {
        genresAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&genre=' + rs[i]["name"] + '">' + rs[i]["name"] + '</a>'+ "   ";
    }
    genresList.append(genresAppend);

}

let charList = jQuery("#charList");
let charAppend = "";
for (let i = 0; i < char_set.length; ++i)
{
    charAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&char=' + char_set[i] + '">' + char_set[i] + '</a>'+ "   ";
}
charList.append(charAppend);


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    if (sessionStorage.getItem(query) == null) {
        console.log("sending AJAX request to backend Java Servlet")
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?query=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function (errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    } else{
        console.log("using cached result");
        handleLookupAjaxSuccess(JSON.parse(sessionStorage.getItem(query)), query, doneCallback)
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    if (sessionStorage.getItem(query)==null){
        sessionStorage.setItem(query,JSON.stringify(data));
    }
    console.log("used suggestion list"+" for query "+ query+": " +data);
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    console.log("you select " + suggestion["value"])
    window.location.replace("single-movie.html?title=" + suggestion["value"]);
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        $("#search_form").submit();
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button



/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/getGenre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenre(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});



jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: to, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
