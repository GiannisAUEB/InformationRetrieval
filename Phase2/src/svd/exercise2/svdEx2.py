#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np


# In[2]:


txd = pd.read_csv("luceneDocVector.csv", header=None, sep='\t')
print(txd)


# In[3]:


txd.to_numpy()


# In[4]:


U, S, V = np.linalg.svd(txd)
S = np.diag(S)
k=50

Uk = U[:, :k]
Sk = S[:k, :k]
Vk = V[:k, :]

Ak = Uk.dot(Sk).dot(Vk)


# In[5]:


print("Rank %d approximation of: " %k)
print(Ak)
print()
print(txd)




