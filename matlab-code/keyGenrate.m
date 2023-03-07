function con = keyGenrate(key)
L=length(key);
%Key = rand(1,L*L)
con=key;
for i=1:L-1
    con=[con key];
end