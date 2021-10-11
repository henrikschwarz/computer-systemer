"""
Opgave 36
"""


def a(pattern):
    """
    :param pattern: pit pattern
    :return:
    """
    return bin(pattern | 0b11110000)


def b(pattern):
    """
    Complement the most significant bit
    :param pattern: bit pattern
    :return:
    """
    return bin(pattern ^ 0b10000000)


def c(pattern):
    """
    Complement 8 bit pattern
    :param pattern: bit pattern
    :return:
    """
    return bin(pattern ^ 0b11111111)


def d(pattern):
    """
    Put a 0 in the least significant bit of an 8-bit pattern without disturbing the other bits.
    :param pattern:
    :return:
    """
    return bin(pattern ^ 0b00000001)


def e(pattern):
    """
    Put 1s in all but the most significant bit of an 8-bit pattern without disturbing the most significant bit.
    :param pattern:
    :return:
    """
    return bin(pattern | 0b01111111)


def f(pattern):
    """
    Filter out all of the green color component from an RGB bitmap image pixel in which the middle 8 bits of a 24-bit
     pattern store the green information.
    :param pattern:
    :return:
    """
    return bin(pattern & 0b111111110000000011111111)


def g(pattern):
    """
    Invert all of the bits in a 24-bit RGB bitmap pixel.
    :param pattern:
    :return:
    """
    return bin(pattern ^ 0b111111111111111111111111)


def h(pattern):
    """
    Set all the bits in a 24-bit RGB bitmap pixel to 1, indicating the color white.
    :param pattern:
    :return:
    """
    return bin(pattern | 0b111111111111111111111111)


print(a(0b00000000))
print(b(0b00000000))
print(b(0b10000000))
print(c(0b10110111))
print(d(0b10100101))
print(e(0b00000000))
print(f(0b101010101010101010101010))
print(g(0b101010101010101010101010))
print(h(0b101010101010101010101010))

# Opgave 39

