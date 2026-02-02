document.addEventListener("DOMContentLoaded", function () {

    let homeSlideIndex = 0;
    const homeSlides = document.querySelectorAll('.home-carousel-slide');

    if (homeSlides.length === 0) return;

    function showHomeSlide(index) {
        homeSlides.forEach((slide, i) => {
            slide.classList.toggle('active', i === index);
        });
        homeSlideIndex = index;
    }

    window.nextHomeSlide = function () {
        let next = (homeSlideIndex + 1) % homeSlides.length;
        showHomeSlide(next);
    }

    window.prevHomeSlide = function () {
        let prev = (homeSlideIndex - 1 + homeSlides.length) % homeSlides.length;
        showHomeSlide(prev);
    }

    // Auto slide
    setInterval(window.nextHomeSlide, 5000);
});
