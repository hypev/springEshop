$(document).ready(function () {
    let ratings = $(".rating");
    if (ratings != null) {
        ratings.each(function () {
            let filledStar = "<i class=\"fas fa-star\"></i>";
            let emptyStar = "<i class=\"far fa-star\"></i>";
            let count = +$(this).data("stars");
            for (let i = 1; i <= 5; i++) {
                if (i <= count) {
                    $(this).append(filledStar);
                } else {
                    $(this).append(emptyStar);
                }
            }
        });
    }
    fetch("/basket/amount")
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            let text = data.response == 0 ? "" : "[ " + data.response + " ]";
            $("#basketAmount").text(text);
        });
});