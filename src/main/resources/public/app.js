$(function(){

    const $tbody = $("#inv tbody");

    function load(){
        $.getJSON('/api/inventory', function(rows){
            $tbody.empty();
            rows.forEach(r => {
                $tbody.append(`<tr><td>${r.type}</td><td>${r.title}</td><td>${r.meta}</td></tr>`);
            });
        });
    }

    $('#seed').on('click', function(){
        $.post('/api/seed').done(load);
    });

    $('#reload').on('click', load);
    load();
});