$(document).ready(function () {
    $('.navbar-nav li').removeClass('active');

    if (window.location.pathname === '/' || window.location.href.endsWith('/home')) {
        $('.navbar-nav #home').addClass('active');
    } else if (window.location.href.endsWith('/beverages')) {
        $('.navbar-nav #beverages').addClass('active');
    } else if (window.location.href.endsWith('/orders')) {
        $('.navbar-nav #orders').addClass('active');
    } else if (window.location.href.endsWith('/cart')) {
        $('.navbar-nav #cart').addClass('active');
    }
});

function updateCartItemCount(cartItemCount) {
    $('#badge-cart-item-count').text(cartItemCount);
}