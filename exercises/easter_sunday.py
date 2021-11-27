import datetime

#provide month number

def easter_sunday(year):
    """
    Implementing the easter sunday algorithm Carl Friedrich Gauss formulated
    """
    y = year
    a = y % 19
    b = y // 100
    c = y % 100
    d = b // 4
    e = b % 4
    g = (8 * b + 13) // 25
    h = (19 * a + b - d - g + 15) % 30
    j = c // 4
    k = c % 4
    m = (a + 11 * h) // 319
    r = (2 * e + 2 * j - k - h + m + 32 )% 7
    n = (h - m + r + 90) // 25
    p = (h - m + r + n + 19) % 32 
    #print(a,b,c,d,e,g,h,n,j,k,p,m,r)
    month_name = datetime.datetime.strptime(str(n), "%m").strftime("%B")
    print("Easter falls on day %d in month %s in year %d" % (p,month_name,year))
    return {"day": p, "month": n}

print(easter_sunday(2001))
print(easter_sunday(1900))
print(easter_sunday(1997))
print(easter_sunday(2017))
print(easter_sunday(2018))
print(easter_sunday(2019))
