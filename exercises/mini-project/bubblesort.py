import random

def bubblesort(l):
    isSorted = False
    while isSorted == False:
        isSorted = True
        for j,k in zip(range(0,len(l)-1), range(1,len(l))):
            if l[j] > l[k]:
                temp_val = l[k]
                l[k]=l[j]
                l[j]=temp_val
                isSorted = False
    return l

numbers = [random.randint(1,100) for i in range(10)]
print(bubblesort(numbers))