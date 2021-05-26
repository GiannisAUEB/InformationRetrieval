#!/usr/bin/env python
# coding: utf-8

# In[56]:


import pandas as pd
import numpy as np


# In[57]:


txd = pd.read_csv("luceneDocVector.csv", header=None, sep='\t')
print(txd)
txq = pd.read_csv("luceneQueVector.csv", header=None, sep='\t')
print(txq)


# In[58]:


txd.to_numpy()


# In[59]:


U, S, V = np.linalg.svd(txd)
S = np.diag(S)
k=50

Uk = U[:, :k]
Sk = S[:k, :k]
Vk = V[:k, :]

Ak = Uk.dot(Sk).dot(Vk)


# In[60]:


print("Rank %d approximation of: " %k)
print(Ak)
print()
print(txd)


# In[65]:


def cosine_similarity(query_id, top):
    q = query_id;

    query_c = txq.iloc[:, q]
    norm_Ak = np.linalg.norm(Ak)
    norm_query = np.linalg.norm(query_c)
    cosineSimilarity = query_c.dot(Ak)/ norm_Ak * norm_query
    sort_indexes = np.argsort(-cosineSimilarity)

    top_docs = sort_indexes[:top]
    top_cos = cosineSimilarity[:top]
    j=0
    for i in top_docs:
        file.write("%d\t0\t%d\t0\t%f\tmyIRmethod\n"%(q+1,i+1,top_cos[j]))
        j=j+1



# In[66]:



top_q = 20

file = open('LISARESULTS_20.test', 'w')
for x in range (0 , 34):
    indexes = cosine_similarity(x, top_q)

file.close()

top_q = 30
file = open('LISARESULTS_30.test', 'w')
for x in range (0 , 34):
    indexes = cosine_similarity(x, top_q)

file.close()

top_q = 50
file = open('LISARESULTS_50.test', 'w')
for x in range (0 , 34):
    indexes = cosine_similarity(x, top_q)

file.close()




