def is_leap_year(year):
    if year % 100 == 0:
        return True if (year % 400 == 0) else False
    if year % 4 == 0:
        return True
    else:
        return False

years_to_test = [400, 200, 2016, 2020, 2015, 2013, 1000, 2000]
for y in years_to_test:
    print("Is %d a leap year: %r" % (y, is_leap_year(y)))