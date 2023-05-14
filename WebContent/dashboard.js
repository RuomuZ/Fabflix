function insert_star(form){
    let l = form.star.value;
    let s = form.birthday.value;
    console.log("submit insert star form");
    console.log(s);
    console.log("api/insertStar?star=" + l + "&birthday="+s);
    $.ajax("api/insertStar?star=" + l + "&birthday="+s, {
        method: "GET",
        success: handleInsertStarResult

    });
}
function handleInsertStarResult(rs)
{
    let resultDataJson = JSON.parse(rs);

    console.log("handle insert star response");
    console.log(resultDataJson);
    console.log(resultDataJson["message"]);
    $("#insert_star_message").text(resultDataJson["message"]);

}
