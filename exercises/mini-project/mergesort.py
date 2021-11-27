def mergesort(l):
    if len(l) > 2:
        a = mergesort(l[len(l)//2:])
        b = mergesort(l[len(l)])