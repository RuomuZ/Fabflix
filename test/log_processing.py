#!/usr/bin/env python
# coding: utf-8

# In[1]:


import sys


# In[10]:


f = open(sys.argv[1], "r")
count = 0
ts_total = 0
tj_total = 0
while True:
    s = f.readline()
    if s == "":
        break
    ts, tj = s.split(",")
    ts_total += int(ts)
    tj_total += int(tj)
    count += 1
print("total number of queries: " + str(count))
print("TS: " + str(ts_total // count))
print("TJ: " + str(tj_total // count))


# In[ ]:




