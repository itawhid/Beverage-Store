$(document).ready(function () {
    updateNavbarActiveLink();
    updateCartItemCount();

    $('#lnk-logout').click(() => {
        $('#form-logout').submit();
    });
});

function updateNavbarActiveLink() {
    $('.navbar-nav li').removeClass('active');

    if (window.location.pathname === '/' || window.location.href.endsWith('/home')) {
        $('.navbar-nav #home').addClass('active');
    } else if (window.location.href.endsWith('/beverage/bottle') || window.location.href.endsWith('/beverage/crate')) {
        $('.navbar-nav #beverages').addClass('active');
    } else if (window.location.href.endsWith('/order')) {
        $('.navbar-nav #orders').addClass('active');
    } else if (window.location.href.endsWith('/cart')) {
        $('.navbar-nav #cart').addClass('active');
    }
}

function updateCartItemCount() {
    $.ajax({
        url: '/api/cart-items/count',
        type: 'GET',
        success: (data) => {
            $('#badge-cart-item-count').text(data);
            $('#txt-cart-item-count').text(data);

            $('input[name ="cartItemCount"]').val(data);
        },
        error: () => {
            $('#badge-cart-item-count').text(0);
            $('#txt-cart-item-count').text(0);

            $('input[name ="cartItemCount"]').val(0);
        }
    });
}

function updateCartTotal() {
    $.ajax({
        url: '/api/cart-items/total-price',
        type: 'GET',
        success: (data) => {
            $('#txt-cart-total').text(data.toFixed(2) + '€');
        }
    });
}

function addItemToCart(beverageId, quantity, isBottle) {
    blockScreen();

    $.ajax({
        url: '/api/cart-items',
        type: 'POST',
        data: JSON.stringify({
            beverageId: beverageId,
            beverageType: isBottle ? 'BOTTLE' : 'CRATE',
            quantity: quantity
        }),
        contentType: 'application/json',
        success: () => {
            unblockScreen();
            updateCartItemCount();
            alertify.success("Item successfully added to the cart.");
        },
        error: () => {
            unblockScreen();
            alertify.error("Error in adding item to the cart.");
        }
    });
}

function removeItemFromCart(cartItemId, successCallback) {
    blockScreen();

    $.ajax({
        url: '/api/cart-items/' + cartItemId,
        type: 'DELETE',
        success: () => {
            unblockScreen();
            successCallback();
            updateCartTotal();
            updateCartItemCount();
            alertify.success('Item successfully removed from the cart');
        },
        error: () => {
            unblockScreen();
            alertify.error("Error in removing item from the cart.");
        }
    });
}

function blockScreen() {
    $('#screen-blocker').show();
}

function unblockScreen() {
    $('#screen-blocker').hide();
}
