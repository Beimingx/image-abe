function flag=Reciver(respath,keypath,addpath) %

%bianhao=15;
%caozuo = 1;
%src1=resimg;
src1 =[respath,'face.bmp'];  %read file
imgEmb = double(imread(src1));
imgExr = imgEmb;
extractnum=0;


con1=load (keypath);
addtion = [addpath,'face.mat'];
load(addtion,'y1','x1','kuan','gao','count','T','ftb','job','ftg','jog','ftr','jor');
% econr=key;
% econg=key;
% econb=key;
con=int8(round(con1));
econ=fliplr(con);


%load ("D:\dcpabe\add-img\15face1.mat");
% load addrgb.mat;
% load critical.mat;
%result=g;   imbEtr
w = fspecial('gaussian',[3,3],0.3);
for a =1 : 3
if(a == 1)
    result =double(imgEmb(:,:,3));
    ft = ftb;
    jo = job;
    %econ = econb;
end
if(a == 2)
    result =double(imgEmb(:,:,2));
    ft = ftg;
    jo = jog;
    %econ = econg;
end
if(a == 3)
   result =double(imgEmb(:,:,1));
   ft = ftr;
   jo = jor;
   %econ = econr;
end      
for p1=1:count
    pp=count-p1+1;
    for aa=x1+1:x1+gao
        ii=(x1+gao)-aa+(x1+1);
        for bb= y1+1 : y1+kuan
            jj=(y1+kuan)-bb+(y1+1);
            if(ft(pp,(x1+gao)-aa+1,(y1+kuan)-bb+1)==0)     
                change=0;
                pre=predict(ii+1,jj+1,result);%中间的进行预测
                pre=double(pre);
                f=result(ii+1,jj+1);%保存下原始的值
                result(ii+1,jj+1)=pre;
                for k=0:2
                    for m=0:2
                       change=change+(result(ii+k,jj+m)*w(1+k,1+m));  %滤波的值
                    end
                end
                %result(i+1,j+1)=f;
                lx=fix(change)-result(ii+1,jj+1);
                err=f-double(pre)-lx;
                err=err*2;
                if(jo(pp,(x1+gao)-aa+1,(y1+kuan)-bb+1)==0)

                else
                    if(err>0)
                        err=err+1;
                    else
                        err=err-1;
                    end
                end
                err=err-double(econ(1,(aa-(x1+1))*kuan+(bb-(y1+1))+1));
                if(err<-(2*T))
                    delta=err+T;
                elseif(err>(2*T)+1)
                    delta=err-T-1;
                else   %(-2<=err<=3)
                    b=mod(err,2);
                    %disp(b);
                    extractnum=extractnum+1;
                    delta=(err-b)/2;                    
                end
                fin=pre+delta;
                result(ii+1,jj+1)=fin;
            end   
        end
    end
end
if(a == 1)
    imgExr(:,:,3)=uint8(result);end
if(a == 2)
    imgExr(:,:,2)=uint8(result);end
if(a == 3)
    imgExr(:,:,1)=uint8(result);end  
end


%subplot(1,2,1),imshow(uint8(imgEmb)),title('携带秘密信息的受保护的载密图像'); 
%subplot(1,2,2),imshow(uint8(imgExr)),title('恢复后图像'); 

recoverimg=[respath, 'recovered.bmp'];  %save recovered image
imwrite(uint8(imgExr),recoverimg);
disp("提取完成！");
flag=1;

